import logging

from app.core.container import Container
from app.infrastructure.websocket import WebSocketConnectionManager
from app.services.interview import InterviewService
from dependency_injector.wiring import Provide, inject
from fastapi import APIRouter, Depends, WebSocket, WebSocketDisconnect

router = APIRouter(tags=["interview"])

logger = logging.getLogger(__name__)


@router.websocket("/ws/interview")
@inject
async def interview_websocket(
    ws: WebSocket,
    ws_connection_manager: WebSocketConnectionManager = Depends(
        Provide[Container.ws_connection_manager]
    ),
    interview_service: InterviewService = Depends(Provide[Container.interview_service]),
):
    # TODO: Replace with actual session ID management logic
    session_id = "temp-session-id"

    await ws_connection_manager.connect(
        session_id=session_id,
        websocket=ws,
    )

    try:
        while True:
            message = await ws_connection_manager.receive(session_id)
            if message is None:
                break

            await interview_service.handle_message(session_id, message)

    except WebSocketDisconnect:
        logger.info(f"Client disconnected: session_id={session_id}")

    except Exception as e:
        logger.error(
            f"WebSocket error for session {session_id}: {e}",
            exc_info=True,
        )

    finally:
        await ws_connection_manager.disconnect(session_id)
