from abc import ABC, abstractmethod


class TTSService(ABC):

    @abstractmethod
    async def synthesize(self, text: str, language: str = "ko") -> bytes:
        pass
