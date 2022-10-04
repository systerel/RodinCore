/*
Package parser allows parsing Rodin files.
*/
package parser

import (
	"bytes"
	"encoding/xml"
	"io"
	"log"
)

// Token is an interface for one of the token types returned by the parser:
// StartElement and EndElement.
type Token any

// StartElement represents the beginning of a Rodin element.
type StartElement struct {
	EName string
	EType string
	Attrs []Attr
}

// Attr represents an attribute of a Rodin element.
type Attr struct {
	Name  string
	Value string
}

// EndElement represents the end of a Rodin element.
type EndElement struct{}

// Parser represents an XML parser reading a Rodin file.  It returns tokens that
// are closer to the Rodin database semantics than a raw XML parser.
type Parser struct {
	fname string       // Rodin file name
	d     *xml.Decoder // Underlying XML decoder
	first bool         // At first start element?
}

// NewParser creates a new parser.
func NewParser(fname string, r io.Reader) *Parser {
	return &Parser{
		fname: fname,
		d:     xml.NewDecoder(r),
		first: true,
	}
}

// Token returns the next token from the Rodin file. Tokens are guaranteed to
// nest properly.  Returns nil at end of file or when a fatal error is
// encountered.
func (p *Parser) Token() Token {
	for {
		tok, err := p.d.Token()
		if err == io.EOF {
			return nil
		}
		if err != nil {
			log.Print(err)
			return nil
		}
		switch tok := tok.(type) {
		case xml.StartElement:
			return p.processStartElement(tok)
		case xml.EndElement:
			return &EndElement{}
		case xml.CharData:
			t := bytes.TrimSpace(tok)
			if len(t) != 0 {
				log.Printf("non-whitespace chardata: %q", t)
			}
		default:
			// ignore
		}
	}
}

func (p *Parser) processStartElement(tok xml.StartElement) *StartElement {
	ename, attrs := processAttributes(tok)
	if p.first {
		p.first = false
		if ename != "" {
			log.Print("first element has a name attribute (ignored)")
		}
		ename = p.fname
	}
	if ename == "" {
		ename = "*missing name*"
	}
	return &StartElement{
		EName: ename,
		EType: tok.Name.Local,
		Attrs: attrs,
	}
}

func processAttributes(tok xml.StartElement) (name string, attrs []Attr) {
	attrs = make([]Attr, len(tok.Attr))
	j := 0
	for _, a := range tok.Attr {
		if a.Name.Local == "name" {
			name = a.Value
			continue
		}
		attrs[j] = Attr{Name: a.Name.Local, Value: a.Value}
		j++
	}
	attrs = attrs[:j]
	return
}
