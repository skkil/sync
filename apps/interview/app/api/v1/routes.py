from app.api.v1.endpoints.interview import router as interview_router
from fastapi import APIRouter

routers = APIRouter()
router_list = [interview_router]

for router in router_list:
    routers.include_router(router)
