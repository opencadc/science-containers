from canfar.sessions import Session
import httpx
import time

class FireFly:
    """
    Class to manage Firefly sessions on CANFAR
    """

    def __init__(self):
        self.session = Session() # type: ignore
        self._url = None

    @property
    def session_info(self) -> dict:
        session_list = self.session.fetch("firefly")
        if len(session_list) == 0:
            self._launch_firefly()
            session_list = self.session.fetch("firefly")
        return session_list[0]

    @property
    def url(self) -> str:
        if self._url is None or httpx.request("HEAD", self._url).status_code != 200:
            self._url = self.get_url()
        return self._url

    def _launch_firefly(self) -> None:
        _ = self.session.create(
            name="firefly",
            image="images.canfar.net/skaha/firefly:2025.2",
            kind="firefly",
        )

    def get_url(self):
        url = self.session_info["connectURL"]
        delay = 0
        # Wait up to 60 seconds for the Firefly instance to become available
        while httpx.request("HEAD", url).status_code != 200 and delay < 60:
            time.sleep(5)
            delay += 5
        return url


firefly_instance = FireFly()
print(firefly_instance.url)