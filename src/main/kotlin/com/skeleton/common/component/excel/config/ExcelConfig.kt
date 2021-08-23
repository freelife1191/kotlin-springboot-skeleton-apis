package com.skeleton.common.component.excel.config

import com.skeleton.common.component.excel.view.ExcelXlsView
import com.skeleton.common.component.excel.view.ExcelXlsxStreamingView
import com.skeleton.common.component.excel.view.ExcelXlsxView
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 컨트롤러에서 엑셀다운로드를 간편하게 하기 위한 설정
 * Created by KMS on 2021/05/25.
 */
@Configuration
class ExcelConfig(
    private val excelXlsView: ExcelXlsView,
    private val excelXlsxView: ExcelXlsxView,
    private val excelXlsxStreamingView: ExcelXlsxStreamingView
): WebMvcConfigurer {

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.enableContentNegotiation(excelXlsxStreamingView, excelXlsxView, excelXlsView)
    }
}