ARG VERSION
FROM jupyter/scipy-notebook:${VERSION}


USER root

RUN curl "https://github.com/uksrc-developers/science-testcases-srcnetv01/blob/main/requirements.txt" -o requirements.txt
RUN pip install -r requirements.txt

