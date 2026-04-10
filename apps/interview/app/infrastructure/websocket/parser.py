from typing import Annotated, Any, Literal, Union

from app.infrastructure.websocket.message import (
    AudioMessage,
    EndInterviewMessage,
    StartInterviewMessage,
    StopRecordingMessage,
)
from pydantic import Field, TypeAdapter

WebSocketMessage = Annotated[
    Union[
        StartInterviewMessage,
        EndInterviewMessage,
        StopRecordingMessage,
        AudioMessage,
    ],
    Field(discriminator="type"),
]


class WebSocketMessageParser:
    @staticmethod
    def parse_json(data: Any) -> WebSocketMessage:
        return TypeAdapter(WebSocketMessage).validate_python(data)

    @staticmethod
    def parse_binary(data: bytes) -> AudioMessage:
        return AudioMessage(data=data)
