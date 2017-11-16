package org.chinlong.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import us.codecraft.webmagic.Spider
import us.codecraft.webmagic.downloader.HttpClientDownloader

@Service
class SpiderService @Autowired constructor(val blackListProcessor: BlackListProcessor) {

    val START_URL = "https://www.xinyongheimingdan.cc"

    fun start() {
        val httpClientDownloader = HttpClientDownloader()
        Spider.create(blackListProcessor)
                .addUrl(START_URL)
                .setDownloader(httpClientDownloader)
                .thread(5)
                .run()
    }

}