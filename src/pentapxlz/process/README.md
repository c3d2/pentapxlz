### Process

* A process is a map that contains at least a :start-fn key with a function
* The *start-fn* needs to take the process map itself as single argument.
* The *start-fn* needs to return a new process map that contains the :stop-fn key (in addition to the other entries)


* Every process should implement the *process.util.resolve/resolve-process* multimethod


* Processes can be renderer, animators or the webserver

#### Process.Util.Registry
* The registry manages all processes. 
* Processes can be registered, started, stopped and unregistered.