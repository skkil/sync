from abc import ABC, abstractmethod


class STTService(ABC):

    @abstractmethod
    async def transcribe(
        self,
        audio_bytes: bytes,
        language: str = "ko",
        detect_language: bool = True,
    ) -> str:
        pass
