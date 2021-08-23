package com.skeleton.common.component.excel.utils

import com.github.drapostolos.typeparser.TypeParser
import com.github.drapostolos.typeparser.TypeParserException
import com.skeleton.common.utils.ValidationUtils
import com.skeleton.common.component.excel.constant.ExcelReaderFieldError
import com.skeleton.common.component.excel.domain.model.ExcelReaderErrorField
import com.skeleton.common.component.excel.service.ExcelReader
import eu.bitwalker.useragentutils.Browser
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.poi.ss.formula.eval.ErrorEval
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Function
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolation
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * Created by KMS on 2021/05/26.
 */
class ExcelUtils {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)

        fun getBrowser(request: HttpServletRequest): String {
            val header = request.getHeader("User-Agent")
            log.info("User-Agent = {}", header)
            return if (header != null) {
                if (header.contains("MSIE") || header.contains("Trident")) {
                    "MSIE"
                } else if (header.contains("Chrome")) {
                    "Chrome"
                } else if (header.contains("Opera")) {
                    "Opera"
                } else if (header.contains("Trident/7.0")) { //IE 11 이상 //IE 버전 별 체크 >> Trident/6.0(IE 10) , Trident/5.0(IE 9) , Trident/4.0(IE 8)
                    "MSIE"
                } else {
                    "Firefox"
                }
            } else {
                "MSIE"
            }
        }

        @Throws(Exception::class)
        fun getDisposition(filename: String, browser: String): String {
            val encodedFilename: String
            when (browser) {
                "MSIE" -> {
                    encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    //encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
                }
                "Firefox" -> {
                    //encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
                    encodedFilename = String(filename.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
                }
                "Opera" -> {
                    //encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
                    encodedFilename = String(filename.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
                }
                "Chrome" -> {
                    val sb = StringBuffer()
                    for (element in filename) {
                        val c = element
                        if (c > '~') {
                            sb.append(URLEncoder.encode("" + c, "UTF-8"))
                        } else {
                            sb.append(c)
                        }
                    }
                    encodedFilename = sb.toString()
                }
                else -> {
                    encodedFilename = String(filename.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
                    //throw new RuntimeException("Not supported browser");
                }
            }
            return encodedFilename
        }

        /**
         * 파일명 Encoder
         */
        private enum class FileNameEncoder(browser: Browser, encodeOperator: UnaryOperator<String>) {
            IE(Browser.IE, UnaryOperator<String> { it: String? ->
                try {
                    return@UnaryOperator URLEncoder.encode(it, StandardCharsets.UTF_8.name())
                        .replace("\\+".toRegex(), "%20")
                } catch (e: UnsupportedEncodingException) {
                    return@UnaryOperator it
                }
            }),
            FIREFOX(Browser.FIREFOX, defaultEncodeOperator), OPERA(
                Browser.OPERA,
                defaultEncodeOperator
            ),
            CHROME(Browser.CHROME, defaultEncodeOperator), UNKNOWN(Browser.UNKNOWN, UnaryOperator.identity<String>());

            private val browser: Browser
            val encodeOperator: UnaryOperator<String>

            companion object {
                private var FILE_NAME_ENCODER_MAP: Map<Browser, Function<String, String>>? = null
                private val defaultEncodeOperator: UnaryOperator<String>
                    get() = UnaryOperator { it: String ->
                        String(
                            it.toByteArray(StandardCharsets.UTF_8),
                            StandardCharsets.ISO_8859_1
                        )
                    }

                fun encode(browser: Browser, fileName: String): String {
                    return FILE_NAME_ENCODER_MAP!![browser]!!.apply(fileName)
                }

                init {
                    FILE_NAME_ENCODER_MAP = EnumSet.allOf(FileNameEncoder::class.java).stream()
                        .collect(
                            Collectors.toMap(
                                { obj: FileNameEncoder -> obj.getBrowser() },
                                { obj: FileNameEncoder -> obj.encodeOperator })
                        )
                }
            }

            protected fun getBrowser(): Browser {
                return browser
            }

            init {
                this.browser = browser
                this.encodeOperator = encodeOperator
            }
        }

        /**
         * Cell 데이터를 Type 별로 체크 하여 String 데이터로 변환함
         * String 데이터로 우선 변환해야 함
         * @param cell 요청 엑셀 파일의 cell 데이터
         * @return String 형으로 변환된 cell 데이터
         */
        fun getValue(cell: Cell): String? {
            if (Objects.isNull(cell) || Objects.isNull(cell.cellType)) return ""
            var value: String
            when (cell.cellType) {
                CellType.STRING -> value = cell.richStringCellValue.string
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) value = cell.localDateTimeCellValue.toString() else value =
                        cell.numericCellValue.toString()
                    if (value.endsWith(".0")) value = value.substring(0, value.length - 2)
                }
                CellType.BOOLEAN -> value = cell.booleanCellValue.toString()
                CellType.FORMULA -> value = cell.cellFormula.toString()
                CellType.ERROR -> value = ErrorEval.getText(cell.errorCellValue.toInt())
                CellType.BLANK, CellType._NONE -> value = ""
                else -> value = ""
            }
            //log.debug("## cellType = {}, value = {}",cell.getCellType(),value);
            return value
        }

        /**
         * TypeParser 로 String으로 변환된 Cell 데이터를 객체 필드 타입에 맞게 변환하여 셋팅해줌
         * @param `object` 요청 객체
         * @param <T>
         * @param row 엑셀 ROW 데이터
         * @return Cell 데이터를 맵핑한 오브젝트
        </T> */
        inline fun <reified T: Any> setObjectMapping(obj: T, row: Row): T {

            val memberProperties = T::class.memberProperties.toList()
            T::class.constructors.forEach {
                it.parameters.forEachIndexed { i, param ->

                    val field: KMutableProperty1<T, *> = memberProperties.first { p -> p.name == param.name } as KMutableProperty1<T, *>

                    var cellValue: String? = null
                    val typeParser: TypeParser = TypeParser.newBuilder().build()
                    try {
                        if (i < row.physicalNumberOfCells) { //유효한 Cell 영역 까지만
                            cellValue = getValue(row.getCell(i))
                            var setData: Any? = null
                            if (!StringUtils.isEmpty(cellValue)) setData =
                                typeParser.parseType(cellValue, field.javaField?.type)
                            field.isAccessible = true
                            field.setter.call(obj, setData)
                            checkValidation(obj, row, i, cellValue, field.name)
                        }
                    } catch (e: TypeParserException) {
                        val error: ExcelReaderFieldError = ExcelReaderFieldError.TYPE
                        ExcelReader.errorFieldList.add(
                            ExcelReaderErrorField(
                                error.name,
                                row.rowNum + 1,
                                field.name,
                                ExcelReader.headerList[i],
                                cellValue,
                                "${error.message} 데이터 필드타입 - ${field.javaField?.type?.simpleName}, 입력값 필드타입 - ${cellValue!!.javaClass.simpleName}",
                                ExceptionUtils.getRootCauseMessage(e)
                            )
                        )
                    } catch (e: Exception) {
                        val error: ExcelReaderFieldError = ExcelReaderFieldError.UNKNOWN
                        ExcelReader.errorFieldList.add(
                            ExcelReaderErrorField(
                                error.name,
                                row.rowNum + 1,
                                field.name,
                                ExcelReader.headerList[i],
                                cellValue,
                                error.message,
                                ExceptionUtils.getRootCauseMessage(e)
                            )
                        )
                    }
                }
            }
            return obj
        }

        /**
         * 객체에 대한 Validation 을 검증해서 검증을 통과 하지 못한 내역이 있을 경우 에러 리스트에 담는다
         * @param `object`
         * @param row
         * @param i
         * @param <T>
        </T> */
        fun <T> checkValidation(obj: T, row: Row, i: Int, cellValue: String?, fieldName: String) {
            val validData: ConstraintViolation<T> = ValidationUtils.getValidData(obj, fieldName) ?: return
            val fieldHeader: String = ExcelReader.headerList.get(i)
            var error: ExcelReaderFieldError = ExcelReaderFieldError.VALID
            var exceptionMessage = validData.message
            if (validData.messageTemplate.contains("NotEmpty") || validData.messageTemplate.contains("NotNull")) {
                error = ExcelReaderFieldError.EMPTY
                exceptionMessage = fieldHeader + "은 필수 입력값입니다"
            }
            ExcelReader.errorFieldList.add(
                ExcelReaderErrorField(
                    error.name,
                    row.rowNum + 1,
                    validData.propertyPath.toString(),
                    fieldHeader,
                    cellValue,
                    error.message,
                    exceptionMessage
                )
            )
        }

        /**
         * 엑셀 업로드 처리를 위한 객체
         * Generic 타입의 객채를 생성하여 업로드된 엑셀 데이터를 객체에 맵핑한다
         * @param row Row
         * @return SampleExcel
         */
        // T::class.java.getDeclaredConstructor().newInstance()
        inline fun <reified T: Any> from(row: Row): T = setObjectMapping(T::class.createInstance(), row)
    }
}