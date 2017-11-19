# pentapxlz

A clojure library to control led-stripes (and other pixels)

## Installation

* Just install [Leiningen](https://leiningen.org/#install)
* git clone https://github.com/c3d2/pentapxlz.git

## Usage

### Start & watch
    $ cd pentapxlz
    $ lein run

### Webapi
    ## View current state on cmdline (via eventstream + ansi-escape-sequences)
    $ curl 'http://localhost:8080/state?target=ledbeere&streamevery=100&ansicolor=true'
    ## For more features read the [API-Documentation](http://localhost:8080/api)

### Interact via [Repl](https://www.clojure.org/guides/learn/syntax#_repl)
    $ cd pentapxlz
    $ lein repl
    > (-main)

    ;; Set rgb values
    > (pentapxlz.pxlz-state/set-rgbPxlz! (repeat [200 200 60]) [:ledball1])
    > (doc pentapxlz.pxlz-state/set-rgbPxlz!)

    ;; Use helper functions to display patterns
    > (use 'pentapxlz.colors 'pentapxlz.mappings.segments)
    > (set-rgbPxlz! (looped (segments [[1 navy] [2 lime] [30 olive]])) [:ledball1])

    ;; Read documentation and sources & try it yourself
    > (source pentapxlz.core/-main)
