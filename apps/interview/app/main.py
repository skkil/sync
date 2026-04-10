from app.api.v1.routes import routers as v1_routers
from app.core.config import config
from app.core.container import Container
from app.core.lifespan import lifespan
from app.core.logging import setup_logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

setup_logging()

app = FastAPI(
    title=config.name,
    version=config.version,
    lifespan=lifespan,
)

container = Container()

app.add_middleware(
    CORSMiddleware,
    allow_origins=config.cors_origins,
    allow_credentials=config.cors_credentials,
    allow_methods=config.cors_methods,
    allow_headers=config.cors_headers,
)


app.include_router(v1_routers)
