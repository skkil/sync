import asyncio
import logging
import os
import tempfile

import torch
import whisper
from app.services.ai.stt.base import STTService
from app.utils.torch import get_torch_device

logger = logging.getLogger(__name__)


class OpenAIWhisperSTTService(STTService):

    _model: whisper.Whisper
    _model_name: str
    _default_language: str

    def __init__(
        self,
        model_name: str = "tiny",
        default_language: str = "ko",
    ):
        self._model_name = model_name
        self._default_language = default_language

        try:
            logger.info(f"Loading Whisper model {self._model_name}")

            device = get_torch_device()
            self._model = whisper.load_model(self._model_name, device=device)

            logger.info(
                f"Whisper model {self._model_name} successfully loaded on {device}"
            )

        except Exception as e:
            logger.error(f"Failed to load Whisper model: {e}", exc_info=True)
            raise

    async def transcribe(
        self,
        audio_bytes: bytes,
        language: str = "ko",
        detect_language: bool = True,
    ) -> str:
        logger.info(f"Transcribing {len(audio_bytes):,} bytes of audio...")

        transcription = await asyncio.to_thread(
            self._transcribe_audio, audio_bytes, language, detect_language
        )

        logger.info(f"Transcription result: '{transcription}'")
        return transcription

    def _transcribe_audio(
        self, audio_bytes: bytes, language: str = "ko", detect_language: bool = False
    ) -> str:
        try:
            with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as temp_file:
                temp_file.write(audio_bytes)
                temp_path = temp_file.name

            try:
                audio = whisper.load_audio(temp_path)
                audio = whisper.pad_or_trim(audio)

                mel = whisper.log_mel_spectrogram(
                    audio, n_mels=self._model.dims.n_mels
                ).to(self._model.device)

                if detect_language:
                    _, probs = self._model.detect_language(mel)
                    detected_lang = max(probs, key=probs.get)
                    logger.info(
                        f"Language detection: {detected_lang} (confidence: {probs[detected_lang]:.2%})"
                    )
                    language = detected_lang

                options = whisper.DecodingOptions(
                    language=language,
                    fp16=torch.cuda.is_available(),
                )
                result = whisper.decode(self._model, mel, options)

                if isinstance(result, list):
                    return " ".join(r.text.strip() for r in result)
                else:
                    return result.text.strip()

            finally:
                try:
                    os.remove(temp_path)
                except Exception:
                    pass

        except Exception as e:
            logger.error(f"Error during transcription: {e}", exc_info=True)
            return ""
