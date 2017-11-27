# pentapxlz

A clojure library to control led-stripes (and other pixels)

## Installation

* Install a current Java
    $ sudo apt install openjdk-8-jre-headless

* Install [Leiningen](https://leiningen.org/#install)
    $ sudo wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein 
    $ sudo chmod +x /usr/local/bin/lein

* Clone pentapxlz
    $ git clone https://github.com/c3d2/pentapxlz.git

## Usage

### Start & watch
    $ cd pentapxlz
    $ lein run

### Webapi
    ## View current state on cmdline (via eventstream + ansi-escape-sequences)
    $ curl 'http://localhost:8080/state?target=ledbeere&streamevery=100&ansicolor=true'

* For more features read the [API-Documentation](http://localhost:8080/api)

### Interact via [Repl](https://www.clojure.org/guides/learn/syntax#_repl)
    $ cd pentapxlz
    $ lein repl
    > (-main)
    > (pentapxlz.process.util.registry/ls-started)
    > (pentapxlz.process.util.registry/stop-all!)
    > (pprint (pentapxlz.process.util.registry/ls))
    > (pentapxlz.process.util.registry/start! :renderer/ledbeere-frame)

    ;; set some pixels
    (set-simple! :state/ledbeere-frame [:green :blue])
    (set-simple! :state/ledbeere-frame (looped (segments [[1 :red] [10 :yellow]])))

    ;; using frame-generators
    (set-simple! :state/ledbeere-frame (resolve-generator {:type :generator/looped
    							   :chain [:generator/segments]
							   :nr+colors [[1 :blue] [10 :yellow]]}))

    ;; Read documentation and sources & try it yourself
    > (source pentapxlz.core/-main)
