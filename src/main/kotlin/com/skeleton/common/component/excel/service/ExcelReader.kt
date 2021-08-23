package com.skeleton.common.component.excel.service

import com.skeleton.common.exception.domain.enums.ErrorCode
import com.skeleton.common.component.excel.domain.model.ExcelReaderErrorField
import com.skeleton.common.component.excel.exception.ExcelReaderException
import com.skeleton.common.component.excel.exception.ExcelReaderFieldException
import com.skeleton.common.component.excel.exception.ExcelReaderFileException
import com.skeleton.common.component.excel.exception.ExcelReaderFileExtentionException
import com.skeleton.common.component.excel.utils.ExcelUtils
import com.skeleton.common.utils.FileUtils
import org.apache.commons.collections4.ListUtils
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * Created by KMS on 2021/05/25.
 */
class ExcelReader {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)

        /** 엑셀 업로드 에러 필드 리스트  */
        var errorFieldList: MutableList<ExcelReaderErrorField> = arrayListOf()

        /** 엑셀 업로드 HEADER 리스트  */
        var headerList: MutableList<String> = arrayListOf()

        /**
         * SAMPLE
         * 사용하기 위해서는 아래와 같이 엑셀 업로드 객체 생성 후 해당 객체
         * from static Constructor 를 생성하고
         * ExcelUtils.setObjectMapping(new Object(), row); 로 리턴 해야 함
         *
         * Sample Product 객체
         */
        /*
        public static Product from(Row row) {
            return ExcelUtils.setObjectMapping(new Product(), row);
        }
        */

        /**
         * 엑셀 파일의 데이터를 읽어서 요청한 오브젝트 타입 리스트에 담아 준다
         * @param multipartFile 엑셀 파일 데이터
         * @param rowFunc cell 데이터를 객체에 셋팅해주는 함수
         * @param <T> 요청 객체 타입
         * @return List<T> 요청 객체 타입의 리스트로 리턴
         * @throws IOException
        </T></T> */
        @Throws(Exception::class)
        fun <T> getObjectList(
            file: File,
            rowFunc: Function<Row, T>
        ): List<T> {
            //헤더 데이터가 ROW가 0에 있고 실제 데이터의 시작 ROW가 1번째 부터인 것으로 판단
            return getObjectList(file, rowFunc, 1)
        }

        /**
         * 엑셀 파일의 데이터를 읽어서 요청한 오브젝트 타입 리스트에 담아 준다
         * @param multipartFile 엑셀 파일 데이터
         * @param rowFunc cell 데이터를 객체에 셋팅해주는 함수
         * @param <T> 요청 객체 타입
         * @return List<T> 요청 객체 타입의 리스트로 리턴
         * @throws IOException
        </T></T> */
        @Throws(Exception::class)
        fun <T> getObjectList(
            multipartFile: MultipartFile,
            rowFunc: Function<Row, T>
        ): List<T> {
            // String tempDir = System.getProperty(property);
            val convertFile = File(Objects.requireNonNull<String>(multipartFile.originalFilename))
            multipartFile.transferTo(convertFile)
            //헤더 데이터가 ROW가 0에 있고 실제 데이터의 시작 ROW가 1번째 부터인 것으로 판단
            return getObjectList(convertFile, rowFunc, 1)
        }

        /**
         * 엑셀 파일의 데이터를 읽어서 요청한 오브젝트 타입 리스트에 담아 준다
         * @param multipartFile 엑셀 파일 데이터
         * @param rowFunc cell 데이터를 객체에 셋팅해주는 함수
         * @param startRowParam 데이터 시작 ROW (Default: 1)
         * @param <T> 요청 객체 타입
         * @return List<T> 요청 객체 타입의 리스트로 리턴
         * @throws IOException
        </T></T> */
        @Throws(Exception::class)
        fun <T> getObjectList(
            multipartFile: MultipartFile,
            rowFunc: Function<Row, T>, startRowParam: Int = 1,
        ): List<T> {
            // String tempDir = System.getProperty(property);
            val convertFile = File(Objects.requireNonNull<String>(multipartFile.originalFilename))
            multipartFile.transferTo(convertFile)
            //헤더 데이터가 ROW가 0에 있고 실제 데이터의 시작 ROW가 1번째 부터인 것으로 판단
            return getObjectList(convertFile, rowFunc, startRowParam)
        }

        /**
         * 엑셀 파일의 데이터를 읽어서 요청한 오브젝트 타입 리스트에 담아 준다
         * @param multipartFile 엑셀 파일 데이터
         * @param rowFunc cell 데이터를 객체에 셋팅해주는 함수
         * @param startRowParam 데이터 시작 ROW (Default: 1)
         * @param <T> 요청 객체 타입
         * @return List<T> 요청 객체 타입의 리스트로 리턴
         * @throws IOException
        </T></T> */
        private fun <T> getObjectList(
            file: File,
            rowFunc: Function<Row, T>, startRowParam: Int = 1,
        ): List<T> {

            if(file.length() <= 0) throw ExcelReaderFileException("처리할 파일의 사이즈가 제로(0) 입니다 파일명이나 경로를 확인하세요")
            if(!file.exists()) throw ExcelReaderFileException("처리할 파일이 없습니다")
            if(Objects.isNull(rowFunc)) throw ExcelReaderException("처리할 ROW 함수가 없습니다")

            var startRow = startRowParam
            errorFieldList = ArrayList<ExcelReaderErrorField>()
            headerList = ArrayList()

            try {
                log.info(
                    "Excel Upload fileInfo :: fileName: {}, fileSize: {} Byte, {} MB",
                    file.name,
                    file.length(),
                    file.length() / 1024 / 1024
                )
            } catch (e: Exception) {
                log.info(
                    "Excel Upload fileInfo :: fileName: {}, fileSize: {}",
                    file.name,
                    "비정상 파일 - 파일 사이즈 측정 불가"
                )
            }
            val originalFileName = file.name
            val originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1)
            if (!(originalFileExtension == "xlsx" || originalFileExtension == "xls")) throw ExcelReaderFileExtentionException(
                "엑셀 파일 확장자가 아닙니다 :: originalFileName = $originalFileName, originalFileExtension = $originalFileExtension"
            )

            // rownum 이 입력되지 않으면 default로 1 번째 라인을 데이터 시작 ROW로 판단
            if (Objects.isNull(startRow)) startRow = 1

            // 엑셀 파일을 Workbook에 담는다
            val workbook: Workbook = try {
                WorkbookFactory.create(file.inputStream())
            } catch (e: IOException) {
                throw ExcelReaderFileException(e.message, e)
            }
            // 시트 수 (첫번째에만 존재시 0)
            val sheet = workbook.getSheetAt(0)
            // 전체 행 수
            val rowCount = sheet.physicalNumberOfRows
            // log.debug("## rowCount = "+rowCount);
            // 헤더 셋팅
            headerList = getHeader(file)
            val objectList =
                (startRow until rowCount)
                    .filter { isPass(sheet.getRow(it)) }
                    .map { rowFunc.apply(sheet.getRow(it)) }
                    .toList()
            if (ListUtils.emptyIfNull(errorFieldList).isNotEmpty())
                throw ExcelReaderFieldException(ErrorCode.EXCEL_READER_FIELD_ERROR.message)

            //임시파일 제거
            FileUtils.deleteFile(file)

            return objectList
        }

/**
 * 해당 ROW에 있는 데이터가 모두 비어있으면 빈 ROW로 판단하고 해당 ROW는 PASS 시킨다
 * @param row
 * @return
 */
private fun isPass(row: Row): Boolean {
    var isPass = false

    (0 until row.physicalNumberOfCells).forEachIndexed { i, it ->
        if (StringUtils.isNotEmpty(ExcelUtils.getValue(row.getCell(i)))) isPass = true
    }
    // log.debug("## row.getPhysicalNumberOfCells() = {}, isPass = {}",row.getPhysicalNumberOfCells(), isPass);
    return isPass
}


        /**
         * 헤더 가져오기
         * 가장 상단에 헤더가 있다면 헤더 정보를 List<String> 에 담아준다
         * @param file 엑셀파일
         * @return List<String> 헤더 리스트
         * @throws IOException
        </String></String> */
        private fun getHeader(file: File): MutableList<String> {
            return getHeader(file, 0) //헤더가 가장 첫번째 라인에 있다고 판단함
        }

        /**
         * 헤더 가져오기
         * 가장 상단에 헤더가 있다면 헤더 정보를 List<String> 에 담아준다
         * @param file 엑셀파일
         * @param rownumParam 헤더가 있는 row number 값
         * @return List<String> 헤더 리스트
         * @throws IOException
        </String></String> */
        private fun getHeader(file: File, rownumParam: Int): MutableList<String> {

            // rownum 이 입력되지 않으면 default로 0 번째 라인을 header 로 판단
            var rownum = rownumParam
            if (Objects.isNull(rownum)) rownum = 0
            val workbook: Workbook = try {
                WorkbookFactory.create(FileInputStream(file))
            } catch (e: IOException) {
                throw ExcelReaderFileException(e.message, e)
            }
            // 시트 수 (첫번째에만 존재시 0)
            val sheet = workbook.getSheetAt(0)

            // 타이틀 가져오기
            val title = sheet.getRow(rownum)
            return IntStream
                .range(0, title.physicalNumberOfCells)
                .mapToObj { cellIndex: Int ->
                    title.getCell(cellIndex).stringCellValue
                }
                .collect(Collectors.toList())
        }

        /**
         * 타이틀 가져오기
         * 가장 상단에 타이틀이 있다면 타이틀 정보를 List<String> 에 담아준다
         * @param file 엑셀파일
         * @return List<String> 타이틀 리스트
         * @throws IOException
        </String></String> */
        fun getTitle(file: File): List<String>? {
            val workbook: Workbook = try {
                WorkbookFactory.create(FileInputStream(file))
            } catch (e: IOException) {
                throw ExcelReaderFileException(e.message, e)
            }
            // 시트 수 (첫번째에만 존재시 0)
            val sheet = workbook.getSheetAt(0)

            // 타이틀 가져오기
            val title = sheet.getRow(0)
            return IntStream
                .range(0, title.physicalNumberOfCells)
                .mapToObj { cellIndex: Int ->
                    title.getCell(cellIndex).stringCellValue
                }
                .collect(Collectors.toList())
        }
    }

}