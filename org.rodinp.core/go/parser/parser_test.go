package parser_test

import (
	"fmt"
	"reflect"
	"strings"
	"testing"

	"rodinp.org/dbviewer/parser"
)

func TestParser(t *testing.T) {
	var cases = []struct {
		name  string         // Name of test case
		fname string         // Name of Rodin file
		input string         // Content of Rodin file
		want  []parser.Token // Expected list of tokens
	}{
		{
			name:  "simplest",
			fname: "foo.buc",
			input: `<foo></foo>`,
			want: []parser.Token{
				start("foo.buc", "foo"),
				end(),
			},
		}, {
			name:  "one child",
			fname: "foo.buc",
			input: `<foo><bar name="n"></bar></foo>`,
			want: []parser.Token{
				start("foo.buc", "foo"),
				start("n", "bar"),
				end(),
				end(),
			},
		}, {
			name:  "one attr",
			fname: "foo.buc",
			input: `<foo f="a"></foo>`,
			want: []parser.Token{
				start("foo.buc", "foo", "f", "a"),
				end(),
			},
		}, {
			name:  "two attrs",
			fname: "foo.buc",
			input: `<foo f="a" g="b"></foo>`,
			want: []parser.Token{
				start("foo.buc", "foo", "f", "a", "g", "b"),
				end(),
			},
		}, {
			name:  "attr on child",
			fname: "foo.buc",
			input: `<foo><bar name="n" f="a"></bar></foo>`,
			want: []parser.Token{
				start("foo.buc", "foo"),
				start("n", "bar", "f", "a"),
				end(),
				end(),
			},
		},
	}

	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			r := strings.NewReader(tc.input)
			p := parser.NewParser(tc.fname, r)
			ts := readAll(p)
			if !reflect.DeepEqual(tc.want, ts) {
				t.Errorf("parser: got\n%vwant\n%v",
					toksToString(ts),
					toksToString(tc.want),
				)
			}
		})
	}
}

func start(ename, etype string, rest ...string) *parser.StartElement {
	if len(rest)%2 != 0 {
		panic("not an even number of arguments")
	}
	as := make([]parser.Attr, len(rest)/2)
	for i := 0; i < len(rest); i += 2 {
		as[i/2] = parser.Attr{Name: rest[i], Value: rest[i+1]}
	}
	return &parser.StartElement{
		EName: ename,
		EType: etype,
		Attrs: as,
	}
}

func end() *parser.EndElement { return &parser.EndElement{} }

func readAll(p *parser.Parser) []parser.Token {
	var ts []parser.Token
	t := p.Token()
	for t != nil {
		ts = append(ts, t)
		t = p.Token()
	}
	return ts
}

func toksToString(ts []parser.Token) string {
	var buf strings.Builder
	for i, t := range ts {
		fmt.Fprintf(&buf, "%-2d: %v\n", i, t)
	}
	return buf.String()
}
