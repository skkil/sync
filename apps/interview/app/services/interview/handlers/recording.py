import logging

from app.infrastructure.websocket.handler import BaseMessageHandler
from app.infrastructure.websocket.manager import WebSocketConnectionManager
from app.infrastructure.websocket.message import AudioMessage, StopRecordingMessage
from app.services.ai.tts.base import TTSService

logger = logging.getLogger(__name__)


class StopRecordingMessageHandler(BaseMessageHandler[StopRecordingMessage]):
    _ws_connection_manager: WebSocketConnectionManager
    _tts_service: TTSService

    def __init__(
        self,
        ws_connection_manager: WebSocketConnectionManager,
        tts_service: TTSService,
    ) -> None:
        self._ws_connection_manager = ws_connection_manager
        self._tts_service = tts_service

    async def handle(self, session_id: str, message: StopRecordingMessage):
        logger.info("Handling StopRecordingMessage")

        data = await self._tts_service.synthesize(
            "안녕하세요, 인터뷰가 종료되었습니다.", language="ko"
        )

        await self._ws_connection_manager.send(
            session_id=session_id, message=AudioMessage(data=data)
        )
