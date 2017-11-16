package org.chinlong

import org.chinlong.services.SpiderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class App @Autowired constructor(val spiderService: SpiderService) : CommandLineRunner {

    @Bean
    fun exitCodeGenerator() = ExitCodeGenerator { 42 }

    override fun run(vararg args: String?) {
        println("run!")
        spiderService.start()
        println("end!")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.exit(SpringApplication.exit(SpringApplication.run(App::class.java, *args)))
        }
    }
}








