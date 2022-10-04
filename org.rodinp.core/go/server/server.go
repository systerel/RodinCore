package server

import (
	_ "embed"
	"html/template"
	"io"
	"log"
	"net/http"
	"strings"
	"unicode/utf8"

	"rodinp.org/dbviewer/parser"
)

func Run(addr string) {
	http.HandleFunc("/", handler)
	log.Fatal(http.ListenAndServe(addr, nil))
}

func handler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		handleGet(w, r)
	case "POST":
		handlePost(w, r)
	default:
		http.Error(w, "unavailable method", 405)
	}
}

//go:embed root.html.tmpl
var getTmplString string
var getTmpl = template.Must(template.New("get").Parse(getTmplString))

func handleGet(w http.ResponseWriter, r *http.Request) {
	err := getTmpl.Execute(w, 0)
	if err != nil {
		log.Print(err)
		http.Error(w, "internal error", 500)
	}
}

//go:embed tree.html.tmpl
var treeTmplString string
var treeTmpl = template.Must(template.New("tree").Funcs(treeFuncs).Parse(treeTmplString))

func handlePost(w http.ResponseWriter, r *http.Request) {
	rd, part, err := r.FormFile("file")
	if err != nil {
		log.Print(err)
		http.Error(w, "bad form", 400)
		return
	}
	data := newPostData(part.Filename, rd)
	err = treeTmpl.Execute(w, data)
	if err != nil {
		log.Print(err)
	}
}

type postData struct {
	Title  string
	Tokens <-chan parser.Token
}

func newPostData(title string, r io.Reader) *postData {
	ch := make(chan parser.Token)

	go parseRodinFile(ch, title, r)
	return &postData{
		Title:  title,
		Tokens: ch,
	}
}

func parseRodinFile(out chan<- parser.Token, fname string, r io.Reader) {
	p := parser.NewParser(fname, r)
	for {
		t := p.Token()
		if t == nil {
			break
		}
		out <- t
	}
	close(out)
}

var treeFuncs = template.FuncMap{
	"is_start": isStart,
	"short":    shortName,
}

func isStart(tok parser.Token) bool {
	_, ok := tok.(*parser.StartElement)
	return ok
}

func shortName(name string) string {
	parts := strings.Split(name, ".")
	var buf strings.Builder
	last := len(parts) - 1
	for i := 0; i < last; i++ {
		r, _ := utf8.DecodeRuneInString(parts[i])
		buf.WriteRune(r)
		buf.WriteByte('.')
	}
	buf.WriteString(parts[last])
	return buf.String()
}
