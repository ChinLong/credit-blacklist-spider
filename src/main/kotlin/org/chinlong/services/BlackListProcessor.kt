package org.chinlong.services

import org.chinlong.exceptions.MsgException
import org.chinlong.models.User
import org.chinlong.models.types.Sex
import org.chinlong.repositories.UserRepository
import org.chinlong.utils.DateTimeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.selector.Html
import java.time.LocalDate

@Component
class BlackListProcessor @Autowired constructor(val userRepository: UserRepository) : PageProcessor {

    private val logger: Logger = LoggerFactory.getLogger(BlackListProcessor::class.java)

    // 匹配URL
    val PAGE_URL = "https://www.xinyongheimingdan.cc/s?p="
    val PAGE_URL_R = "https://www\\.xinyongheimingdan\\.cc/s\\?p=\\d+"

    private val site = Site.me()
            .setDomain("https://www.xinyongheimingdan.cc/")
            .setSleepTime(1000)
            .setRetryTimes(30)
            .setCharset("utf-8")
            .setTimeOut(30000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.89 Safari/537.36")

    override fun getSite(): Site = site

    override fun process(page: Page) {
        val html = page.html
        if (!page.getUrl().regex(PAGE_URL_R).match()) {
            // 获取页数
            val lastPage = html.xpath("//a[@class='end']/text()").get().toInt()
            // 保存结果
            saveResults(html)
            // 拼接URLs
            val urls = (2..lastPage).toList().map { i -> PAGE_URL + i.toString() }
            // 加入请求
            page.addTargetRequests(urls)
        } else {
            saveResults(html)
        }
    }

    protected fun saveResults(html: Html) {
        html.xpath("//table/tbody/tr[@class='pointer']").nodes().forEach { node ->
            val nodes = node.xpath("//tr/td/text()").all()
            val id = nodes[1].trim().toUpperCase()
            val name = nodes[0].trim()
            val telNo = nodes[2].trim()
            val money = nodes[3].trim()
            val status = nodes[4].trim()
            checkUser(id, name, telNo, money, status)
        }

    }

    private val DOUBLE_PATTERN = "[\\d.]+".toRegex()
    private val NUMBER_PATTERN = "[\\d]+".toRegex()

    private fun checkUser(id: String, name: String, telNo: String, loan: String, status: String) {
        try {
            if (id.length != 18) {
                throw MsgException("[Exception] => (id.length != 18).")
            }
            if (!NUMBER_PATTERN.matches(id.dropLast(1))) {
                throw MsgException("[Exception] => (id doesn't matches number).")
            }
            val now = LocalDate.now()
            val sex = if (id.substring(16, 17).toInt() % 2 == 0) Sex.MALE else Sex.FEMALE
            val address = id.take(5)
            val birthday = DateTimeUtil.parseDate(id.substring(6, 14), format = DateTimeUtil.yyyyMMdd)
            val moneyStr = DOUBLE_PATTERN.findAll(loan).map { v -> v.value }.joinToString(separator = "").trim()
            val dayStr = NUMBER_PATTERN.findAll(status).map { v -> v.value }.joinToString(separator = "").trim()
            val money = if (moneyStr.isEmpty()) 0 else moneyStr.toDouble().toInt()
            val day = if (dayStr.isEmpty()) 0L else dayStr.toLong()

            val user = User(
                    id = id,
                    name = name,
                    sex = sex.flg,
                    birthday = birthday,
                    telNo = telNo,
                    address = address,
                    money = money,
                    shouldPayAt = now.plusDays(-day)
            )
            val dbUser = userRepository.findOne(id)
            if (null == dbUser)
                userRepository.save(user)
            else if (user != dbUser) {
                if (user.money > dbUser.money) {
                    userRepository.save(user)
                }
                val record = "[REPETITION_RECORD] => { 'name' : '$name', 'id' : '$id', 'telNo' : '$telNo', 'loan': '$loan', 'status' : '$status' }"
                logger.warn(record)
            }
        } catch (e: Exception) {
            val record = "[ERROR_RECORD] => { 'name' : '$name', 'id' : '$id', 'telNo' : '$telNo', 'loan': '$loan', 'status' : '$status' }"
            logger.error(record)
        }

    }

}