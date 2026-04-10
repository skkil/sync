import logging

from app.infrastructure.websocket.message import (
    AudioMessage,
    BaseMessage,
    StopRecordingMessage,
    WebSocketMessageType,
)
from app.services.interview.handlers import (
    AudioMessageHandler,
    StopRecordingMessageHandler,
)

logger = logging.getLogger(__name__)


class InterviewService:
    _audio_message_handler: AudioMessageHandler
    _stop_recording_message_handler: StopRecordingMessageHandler

    def __init__(
        self,
        audio_data_message_handler: AudioMessageHandler,
        stop_recording_message_handler: StopRecordingMessageHandler,
    ) -> None:
        self._audio_data_message_handler = audio_data_message_handler
        self._stop_recording_message_handler = stop_recording_message_handler

    async def handle_message(self, session_id: str, message: BaseMessage) -> None:
        logger.debug(f"Routing message from (session_id={session_id})")
        match message.get_type():
            case WebSocketMessageType.AUDIO:
                if not isinstance(message, AudioMessage):
                    return

                await self._audio_data_message_handler.handle(
                    session_id=session_id, message=message
                )

            case WebSocketMessageType.STOP_RECORDING:
                if not isinstance(message, StopRecordingMessage):
                    return

                await self._stop_recording_message_handler.handle(
                    session_id=session_id, message=message
                )
