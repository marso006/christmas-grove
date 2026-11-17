"""A minimal writer for Minecraft's NBT format (gzip-compressed, big-endian), enough for structure
templates. Values are wrapped in small classes so the tag type is explicit."""
from __future__ import annotations

import gzip
import struct
from dataclasses import dataclass
from typing import Any


@dataclass
class Byte:
    value: int


@dataclass
class Int:
    value: int


@dataclass
class Float:
    value: float


@dataclass
class Double:
    value: float


@dataclass
class String:
    value: str


@dataclass
class List:
    item_type: int  # tag id of the elements
    items: list


class Compound(dict):
    pass


TAG_END, TAG_BYTE, TAG_INT, TAG_FLOAT, TAG_DOUBLE, TAG_STRING, TAG_LIST, TAG_COMPOUND = 0, 1, 3, 5, 6, 8, 9, 10


def tag_id(value: Any) -> int:
    if isinstance(value, Byte):
        return TAG_BYTE
    if isinstance(value, Int):
        return TAG_INT
    if isinstance(value, Float):
        return TAG_FLOAT
    if isinstance(value, Double):
        return TAG_DOUBLE
    if isinstance(value, String):
        return TAG_STRING
    if isinstance(value, List):
        return TAG_LIST
    if isinstance(value, Compound):
        return TAG_COMPOUND
    raise TypeError(f"not an NBT value: {value!r}")


def _string(s: str) -> bytes:
    data = s.encode("utf-8")
    return struct.pack(">H", len(data)) + data


def _payload(value: Any) -> bytes:
    if isinstance(value, Byte):
        return struct.pack(">b", value.value)
    if isinstance(value, Int):
        return struct.pack(">i", value.value)
    if isinstance(value, Float):
        return struct.pack(">f", value.value)
    if isinstance(value, Double):
        return struct.pack(">d", value.value)
    if isinstance(value, String):
        return _string(value.value)
    if isinstance(value, List):
        out = struct.pack(">bi", value.item_type if value.items else TAG_END, len(value.items))
        return out + b"".join(_payload(v) for v in value.items)
    if isinstance(value, Compound):
        out = b""
        for key, v in value.items():
            out += struct.pack(">b", tag_id(v)) + _string(key) + _payload(v)
        return out + struct.pack(">b", TAG_END)
    raise TypeError(f"not an NBT value: {value!r}")


def write(path, root: Compound) -> None:
    data = struct.pack(">b", TAG_COMPOUND) + _string("") + _payload(root)
    with open(path, "wb") as raw, gzip.GzipFile(fileobj=raw, mode="wb", mtime=0) as f:
        f.write(data)  # mtime=0 keeps the files byte-for-byte reproducible
