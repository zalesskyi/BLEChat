package com.zalesskyi.android.data.converters.base

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import org.json.JSONArray

/**
 * Encapsulate logic for converting from one type to another and vice versa

 * @param <IN>  Input type
 * *
 * @param <OUT> Output type
</OUT></IN> */
interface Converter<IN, OUT> {

    fun convertInToOut(inObject: IN?): OUT?

    fun convertOutToIn(outObject: OUT?): IN?

    fun convertListInToOut(inObjects: List<IN>?): List<OUT>?

    fun convertListOutToIn(outObjects: List<OUT>?): List<IN>?

    fun observableINtoOUT(): ObservableTransformer<IN?, OUT?>

    fun observableOUTtoIN(): ObservableTransformer<OUT?, IN?>

    fun listObservableINtoOUT(): ObservableTransformer<List<IN>?, List<OUT>?>

    fun listObservableOUTtoIN(): ObservableTransformer<List<OUT>?, List<IN>?>

    fun flowINtoOUT(): FlowableTransformer<IN?, OUT?>

    fun flowOUTtoIN(): FlowableTransformer<OUT?, IN?>

    fun listFlowINtoOUT(): FlowableTransformer<List<IN>?, List<OUT>?>

    fun listFlowOUTtoIN(): FlowableTransformer<List<OUT>?, List<IN>?>

    fun singleOUTtoIN(): SingleTransformer<OUT?, IN?>

    fun singleINtoOUT(): SingleTransformer<IN?, OUT?>

    fun listSingleOUTtoIN(): SingleTransformer<List<OUT>?, List<IN>?>

    fun listSingleINtoOUT(): SingleTransformer<List<IN>?, List<OUT>?>

    fun jsonArraySingleINtoOUT(): SingleTransformer<JSONArray, List<OUT>>
}
