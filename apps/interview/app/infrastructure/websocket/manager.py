import json
import logging

from app.infrastructure.websocket.message import BaseMessage, EmptyMessage
from app.infrastructure.websocket.parser import WebSocketMessageParser
from fastapi import WebSocket, WebSocketDisconnect
from fastapi.websockets import WebSocketState

logger = logging.getLogger(__name__)


class WebSocketConnectionManager:
    _active_connections: dict[str, WebSocket]
    _message_parser: WebSocketMessageParser

    def __init__(self, message_parser: WebSocketMessageParser) -> None:
        self._active_connections = {}
        self._message_parser = message_parser

    async def connect(self, session_id: str, websocket: WebSocket) -> None:
        await websocket.accept()

        if session_id in self._active_connections:
            logger.debug(f"Session {session_id} exists, disconnecting old connection")
            await self.disconnect(session_id)

        self._active_connections[session_id] = websocket
        logger.debug(f"Connection established (session_id={session_id})")

    async def disconnect(self, session_id: str) -> None:
        if session_id not in self._active_connections:
            logger.debug(f"Session {session_id} does not exist, cannot disconnect")
            return

        ws = self._active_connections.pop(session_id)
        if ws.client_state != WebSocketState.DISCONNECTED:
            await ws.close()

        logger.debug(f"Connection closed (session_id={session_id})")

    async def send(self, session_id: str, message: BaseMessage):
        if session_id not in self._active_connections:
            logger.debug(f"Session {session_id} does not exist, cannot send message")
            return

        connection = self._active_connections[session_id]

        if connection.client_state != WebSocketState.CONNECTED:
            logger.debug(f"Session {session_id} is not connected, cannot send message")
            return

        data = message.get_data()

        try:
            if isinstance(data, bytes):
                await connection.send_bytes(data)
            else:
                await connection.send_json(data)
        except Exception as e:
            logger.error(
                f"Error sending message to session {session_id}: {e}", exc_info=True
            )

    async def receive(self, session_id: str) -> BaseMessage | None:
        if session_id not in self._active_connections:
            logger.debug(f"Session {session_id} does not exist, cannot receive message")
            return None

        connection = self._active_connections[session_id]
        if connection.client_state != WebSocketState.CONNECTED:
            logger.debug(
                f"Session {session_id} is not connected, cannot receive message"
            )
            return None

        try:
            data = await connection.receive()

            if "text" in data:
                data = json.loads(data["text"])
                return self._message_parser.parse_json(data)
            elif "bytes" in data:
                return self._message_parser.parse_binary(data["bytes"])
            else:
                logger.debug("Unknown message type received, ignoring")

            return EmptyMessage()

        except json.JSONDecodeError:
            logger.error(f"Invalid JSON format for session {session_id}")
            return EmptyMessage()

        except WebSocketDisconnect:
            await self.disconnect(session_id)
            raise
