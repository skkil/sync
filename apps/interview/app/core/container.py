from app.infrastructure.websocket import WebSocketConnectionManager
from app.infrastructure.websocket.parser import WebSocketMessageParser
from app.services.ai.stt.whisper import OpenAIWhisperSTTService
from app.services.ai.tts.chatterbox import ChatterBoxTTSService
from app.services.interview import InterviewService
from app.services.interview.handlers import (
    AudioMessageHandler,
    StopRecordingMessageHandler,
)
from dependency_injector import containers, providers


class Container(containers.DeclarativeContainer):

    wiring_config = containers.WiringConfiguration(
        modules=["app.api.v1.endpoints.interview"]
    )

    ws_message_parser = providers.Singleton(WebSocketMessageParser)

    ws_connection_manager = providers.Singleton(
        WebSocketConnectionManager,
        message_parser=ws_message_parser,
    )

    stt_service = providers.Singleton(OpenAIWhisperSTTService)
    tts_service = providers.Singleton(ChatterBoxTTSService)

    audio_data_message_handler = providers.Singleton(
        AudioMessageHandler,
        ws_connection_manager=ws_connection_manager,
        stt_service=stt_service,
    )

    stop_recording_message_handler = providers.Singleton(
        StopRecordingMessageHandler,
        ws_connection_manager=ws_connection_manager,
        tts_service=tts_service,
    )

    interview_service = providers.Singleton(
        InterviewService,
        audio_data_message_handler=audio_data_message_handler,
        stop_recording_message_handler=stop_recording_message_handler,
    )
