import logging
from contextlib import asynccontextmanager

from app.core.container import Container
from fastapi import FastAPI

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starting up application")

    yield

    logger.info("Shutting down application")
