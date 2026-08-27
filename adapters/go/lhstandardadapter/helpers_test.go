package lhstandardadapter

import (
	"encoding/json"
	"time"
)

// timeFromYMDH creates a time.Time at UTC+8 for testing.
func timeFromYMDH(year, month, day, hour int) time.Time {
	loc := time.FixedZone("CST", cstOffset*3600)
	return time.Date(year, time.Month(month), day, hour, 0, 0, 0, loc)
}

func jsonMarshalImpl(v interface{}) ([]byte, error) {
	return json.Marshal(v)
}

func jsonUnmarshalImpl(b []byte, v interface{}) error {
	return json.Unmarshal(b, v)
}
