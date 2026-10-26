FROM python:3.13-alpine

COPY requirements.txt ./
COPY .env.example ./.env
COPY webcompstore-cli.py ./
COPY wait-for ./

RUN pip install --no-cache-dir -r requirements.txt
