from abc import ABC, abstractmethod
from typing import Generic, TypeVar

from app.infrastructure.websocket.message import BaseMessage

M = TypeVar("M", bound=BaseMessage)


class BaseMessageHandler(ABC, Generic[M]):

    @abstractmethod
    async def handle(self, session_id: str, message: M):
        pass
