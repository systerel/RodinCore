package main

import (
	"fmt"

	"rodinp.org/dbviewer/server"
)

func main() {
	// TODO implement flag for address
	addr := "localhost:8080"

	fmt.Printf("Starting server on: http://%s\n", addr)
	server.Run(addr)
}
