import logging

from app.infrastructure.websocket.handler import BaseMessageHandler
from app.infrastructure.websocket.manager import WebSocketConnectionManager
from app.infrastructure.websocket.message import AudioMessage
from app.services.ai.stt.base import STTService

logger = logging.getLogger(__name__)


class AudioMessageHandler(BaseMessageHandler[AudioMessage]):
    _ws_connection_manager: WebSocketConnectionManager
    _stt_service: STTService

    def __init__(
        self,
        ws_connection_manager: WebSocketConnectionManager,
        stt_service: STTService,
    ) -> None:
        self._ws_connection_manager = ws_connection_manager
        self._stt_service = stt_service

    async def handle(self, session_id: str, message: AudioMessage):
        transcription = await self._stt_service.transcribe(message.get_data())
        logger.info(f"Transcription result: '{transcription}'")
