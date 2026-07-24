import json
import hashlib
import time
from typing import Dict, Any

class LongHunAdapter:
    def __init__(self, uid: str = "9622", device: str = "HM-9622-001"):
        """
        Initialize the LongHunAdapter with a unique identifier and device name.

        Args:
            uid (str): Unique identifier. Defaults to "9622".
            device (str): Device name. Defaults to "HM-9622-001".
        """
        self.uid = uid
        self.device = device

    def wrap(self, data: Dict[str, Any], task_type: str = "default", persona: str = "P04") -> Dict[str, Any]:
        """
        Wrap the input data with additional metadata.

        Args:
            data (Dict[str, Any]): Input data to be wrapped.
            task_type (str): Task type. Defaults to "default".
            persona (str): Persona. Defaults to "P04".

        Returns:
            Dict[str, Any]: Wrapped data.
        """
        wrapped_data = {
            "uid": self.uid,
            "device": self.device,
            "task_type": task_type,
            "persona": persona,
            "timestamp": int(time.time()),
            "data": data
        }
        return wrapped_data

    def validate(self, wrapped: Dict[str, Any]) -> bool:
        """
        Validate the wrapped data.

        Args:
            wrapped (Dict[str, Any]): Wrapped data to be validated.

        Returns:
            bool: True if the wrapped data is valid, False otherwise.
        """
        required_keys = ["uid", "device", "task_type", "persona", "timestamp", "data"]
        if not all(key in wrapped for key in required_keys):
            return False
        if not isinstance(wrapped["timestamp"], int) or wrapped["timestamp"] < 0:
            return False
        if not isinstance(wrapped["data"], dict):
            return False
        return True

    def schemas(self) -> Dict[str, Any]:
        """
        Get the schema of the wrapped data.

        Returns:
            Dict[str, Any]: Schema of the wrapped data.
        """
        schema = {
            "type": "object",
            "properties": {
                "uid": {"type": "string"},
                "device": {"type": "string"},
                "task_type": {"type": "string"},
                "persona": {"type": "string"},
                "timestamp": {"type": "integer"},
                "data": {"type": "object"}
            },
            "required": ["uid", "device", "task_type", "persona", "timestamp", "data"]
        }
        return schema


def test_long_hun_adapter():
    adapter = LongHunAdapter()
    data = {"key": "value"}
    wrapped = adapter.wrap(data)
    assert adapter.validate(wrapped)
    assert adapter.schemas() == {
        "type": "object",
        "properties": {
            "uid": {"type": "string"},
            "device": {"type": "string"},
            "task_type": {"type": "string"},
            "persona": {"type": "string"},
            "timestamp": {"type": "integer"},
            "data": {"type": "object"}
        },
        "required": ["uid", "device", "task_type", "persona", "timestamp", "data"]
    }


def test_long_hun_adapter_invalid_data():
    adapter = LongHunAdapter()
    data = "invalid"
    wrapped = adapter.wrap(data)
    assert not adapter.validate({"uid": "invalid"})

if __name__ == "__main__":
    test_long_hun_adapter()
    test_long_hun_adapter_invalid_data()