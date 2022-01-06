package example.request.attribute.bug

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("example.request.attribute.bug")
		.start()
}

