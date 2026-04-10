import io
import logging

import soundfile as sf
from app.services.ai.tts.base import TTSService
from app.utils.torch import get_torch_device
from chatterbox.mtl_tts import ChatterboxMultilingualTTS

logger = logging.getLogger(__name__)


class ChatterBoxTTSService(TTSService):

    def __init__(self):
        device = get_torch_device()

        self._model = ChatterboxMultilingualTTS.from_pretrained(
            device=device,
        )

    async def synthesize(self, text: str, language: str = "ko") -> bytes:
        wav = self._model.generate(text, language_id=language)

        buffer = io.BytesIO()
        sf.write(buffer, wav[0], self._model.sr, format="wav")

        wav_bytes = buffer.getvalue()
        return wav_bytes
