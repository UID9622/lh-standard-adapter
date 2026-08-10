import json
import hashlib
import time
from datetime import datetime

class LongHunAdapter:
    def __init__(self, uid="9622", device="HM-9622-001"):
        self.uid = uid
        self.device = device

    def wrap(self, data, task_type="default", persona="P04"):
        wrapped_data = {
            "uid": self.uid,
            "device": self.device,
            "task_type": task_type,
            "persona": persona,
            "data": data,
            "timestamp": int(time.time())
        }
        return wrapped_data

    def validate(self, wrapped):
        required_fields = ["uid", "device", "task_type", "persona", "data", "timestamp"]
        for field in required_fields:
            if field not in wrapped:
                return False
        return True

    def schemas(self):
        return {
            "uid": "string",
            "device": "string",
            "task_type": "string",
            "persona": "string",
            "data": "object",
            "timestamp": "integer"
        }

def main():
    adapter = LongHunAdapter()
    data = {"key": "value"}
    wrapped_data = adapter.wrap(data)
    print(json.dumps(wrapped_data, indent=4))
    print(adapter.validate(wrapped_data))
    print(adapter.schemas())

if __name__ == "__main__":
    main()