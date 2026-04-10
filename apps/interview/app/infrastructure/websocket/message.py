from abc import ABC, abstractmethod
from enum import StrEnum
from typing import Literal

from pydantic import BaseModel


class WebSocketMessageType(StrEnum):
    START_INTERVIEW = "START_INTERVIEW"
    END_INTERVIEW = "END_INTERVIEW"
    STOP_RECORDING = "STOP_RECORDING"
    EMPTY = "EMPTY"
    AUDIO = "AUDIO"


class BaseMessage(ABC, BaseModel):
    @abstractmethod
    def get_type(self) -> WebSocketMessageType:
        pass

    @abstractmethod
    def get_data(self) -> bytes | str | None:
        pass


class EmptyMessage(BaseMessage):
    type: Literal[WebSocketMessageType.EMPTY] = WebSocketMessageType.EMPTY

    def get_type(self) -> Literal[WebSocketMessageType.EMPTY]:
        return self.type

    def get_data(self) -> bytes | str | None:
        return None


class StartInterviewMessage(BaseMessage):
    type: Literal[WebSocketMessageType.START_INTERVIEW] = (
        WebSocketMessageType.START_INTERVIEW
    )

    def get_type(self) -> Literal[WebSocketMessageType.START_INTERVIEW]:
        return self.type

    def get_data(self) -> bytes | str | None:
        return None


class EndInterviewMessage(BaseMessage):
    type: Literal[WebSocketMessageType.END_INTERVIEW] = (
        WebSocketMessageType.END_INTERVIEW
    )

    def get_type(self) -> Literal[WebSocketMessageType.END_INTERVIEW]:
        return self.type

    def get_data(self) -> bytes | str | None:
        return None


class StopRecordingMessage(BaseMessage):
    type: Literal[WebSocketMessageType.STOP_RECORDING] = (
        WebSocketMessageType.STOP_RECORDING
    )

    def get_type(self) -> Literal[WebSocketMessageType.STOP_RECORDING]:
        return self.type

    def get_data(self) -> bytes | str | None:
        return None


class AudioMessage(BaseMessage):
    type: Literal[WebSocketMessageType.AUDIO] = WebSocketMessageType.AUDIO
    _data: bytes

    def __init__(self, data: bytes):
        super().__init__()
        self._data = data

    def get_type(self) -> Literal[WebSocketMessageType.AUDIO]:
        return self.type

    def get_data(self) -> bytes:
        return self._data
