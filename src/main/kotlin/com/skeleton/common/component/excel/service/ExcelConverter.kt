package com.skeleton.common.component.excel.service

import com.skeleton.common.utils.JsonUtils
import com.skeleton.common.component.excel.constant.ExcelConstant
import com.skeleton.common.component.excel.domain.dto.ReqExcelDownload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Created by KMS on 2021/05/25.
 */
@Component
class ExcelConverter {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 엑셀 데이터 생성 헤더 요청 값이 없을 떄
     * @param list
     * @param <E>
     * @return
    </E> */
    fun <E> convertList(list: MutableList<E>): Map<String, Any?> {
        return convertList(list, ReqExcelDownload())
    }

    /**
     * 엑셀 데이터 생성 헤더 요청 값이 있을 때
     * @param list
     * @param reqData
     * @param <E>
     * @param <T>
     * @return
    </T></E> */
    fun <E> convertList(list: List<E>, reqData: ReqExcelDownload): Map<String, Any?> {
        val resultMap: MutableMap<String, Any?> = HashMap()
        var dataMapList: MutableList<Map<String, Any>> = mutableListOf()
        if (list.isEmpty()) {
            return mutableMapOf()
        }

        /* Body 셋팅 */
        //Map이면 Map 데이터 그대로 진행 오브젝트면 Map으로 변환
        if (list[0] is Map<*, *>) {
            dataMapList = (list as List<Map<String, Any>>).toMutableList()
        } else {
            for (obj in list) {
                // 객체 리스트 Map으로 컨버팅
                val map: LinkedHashMap<*, *> = JsonUtils.getObjectMapper().convertValue(
                    obj,
                    LinkedHashMap::class.java
                )
                dataMapList.add(map as Map<String, Any>)
            }
        }
        val body = getBody(dataMapList)

        /* Header 셋팅 */
        var header: MutableList<String> = ArrayList()
        if (reqData.header.isNotEmpty()) {
            for (str in reqData.header) {
                log.debug("header : $str")
                header.add(str)
            }
        } else {
            header = getDefaultHeader(dataMapList)
        }
        resultMap[ExcelConstant.TITLE] = reqData.title
        resultMap[ExcelConstant.FILE_NAME] = if (!reqData.fileName.isNullOrEmpty()) reqData.fileName else "export"
        resultMap[ExcelConstant.HEADER] = header
        resultMap[ExcelConstant.BODY] = body
        resultMap[ExcelConstant.COLOMN_WIDTH] = reqData.columnWidth
        resultMap[ExcelConstant.STYLE] = reqData.style
        resultMap[ExcelConstant.AUTO_SIZING] = reqData.autoSize
        return resultMap
    }

    /**
     * 조회 결과 값 엑셀 데이터 헤더 형식으로 converting
     * @Method Name : convertMap
     * @param rows
     * @return
     * @throws Exception
     */
    private fun getDefaultHeader(rows: List<Map<String, Any>>): MutableList<String> {
        val resultList: MutableList<String> = ArrayList()
        val i = intArrayOf(0)
        if (rows.isEmpty()) return resultList
        log.debug("ROW SIZE = " + rows.size)
        if (rows[0].isEmpty() || "NULL" == rows[0].keys.iterator().next()) {
            return resultList
        }
        log.debug("ROW.get(0) SIZE = " + rows[0].size)
        rows[0].forEach { (k: String, v: Any) ->
            if (k.isNotEmpty()) {
                //                log.debug("## KEYNAME : "+k+" | VALUE : "+rows.get(k)+" | class : "+rows.get(k).getClass().getName());
                //                 log.info("NAME : "+rows.get(k).getClass().getName());
                log.debug("HEADER [{}] KEY : {}, VALUE : {}", i, k, v)
                resultList.add(k)
            }
            i[0]++
        }
        return resultList
    }

    /**
     * 조회 결과 값 엑셀 데이터 바디 형식으로 converting
     * @Method Name : convertMap
     * @param rows
     * @return
     * @throws Exception
     */
    private fun getBody(rows: List<Map<String, Any>>): List<List<String>> {
        val resultList: MutableList<List<String>> = ArrayList()
        if (rows.isEmpty()) return resultList
        log.debug("## ROW SIZE = " + rows.size)
        if (rows[0].isEmpty() || "NULL" == rows[0].keys.iterator().next()) {
            return resultList
        }
        val i = intArrayOf(0)
        for (row in rows) {
            val dataList: MutableList<String> = ArrayList()
            row.forEach { (k: String, v: Any?) ->
                try {
                    if (k.isNotEmpty() && v != null) {
                        // log.debug("## VALUE : ${row[k]} | class : ${ row[k]?.javaClass?.simpleName }")
                        // log.debug("## KEYNAME : {}",k)
                        // log.debug("## KEYNAME : {} | VALUE : {} | class : {}",k, row[k],row[k]?.javaClass?.simpleName)
                        // log.info("NAME : ${row[k]?.javaClass?.simpleName}")
                        // log.debug("LIST [{}] KEY : {}, VALUE : {}",i,k,v)
                        dataList.add(v.toString())
                    } else dataList.add("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            resultList.add(dataList)
            i[0]++
        }
        return resultList
    }
}