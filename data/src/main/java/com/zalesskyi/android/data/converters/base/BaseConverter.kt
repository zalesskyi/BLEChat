package com.zalesskyi.android.data.converters.base

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import org.json.JSONArray
import org.json.JSONObject

/**
 * Base implementation of [Converter]

 * @param <IN>  Input type
 * *
 * @param <OUT> Output type
</OUT></IN> */
abstract class BaseConverter<IN : Any, OUT : Any> : Converter<IN, OUT> {

    override fun convertInToOut(inObject: IN?): OUT? = inObject?.let { processConvertInToOut(it) }

    override fun convertOutToIn(outObject: OUT?): IN? = outObject?.let { processConvertOutToIn(it) }

    override fun convertListInToOut(inObjects: List<IN>?): List<OUT> = inObjects?.mapNotNull { convertInToOut(it) }
            ?: listOf()

    override fun convertListOutToIn(outObjects: List<OUT>?): List<IN> = outObjects?.mapNotNull { convertOutToIn(it) }
            ?: listOf()

    protected abstract fun processConvertInToOut(inObject: IN): OUT?

    protected abstract fun processConvertOutToIn(outObject: OUT): IN?

    override fun observableINtoOUT(): ObservableTransformer<IN?, OUT?> = ObservableTransformer { inObservable ->
        inObservable.map { convertInToOut(it) }
    }

    override fun observableOUTtoIN(): ObservableTransformer<OUT?, IN?> = ObservableTransformer { outObservable ->
        outObservable.map { convertOutToIn(it) }
    }

    override fun listObservableINtoOUT(): ObservableTransformer<List<IN>?, List<OUT>?> = ObservableTransformer { inObservable ->
        inObservable.map { convertListInToOut(it) }
    }

    override fun listObservableOUTtoIN(): ObservableTransformer<List<OUT>?, List<IN>?> = ObservableTransformer { outObservable ->
        outObservable.map { convertListOutToIn(it) }
    }

    override fun flowINtoOUT(): FlowableTransformer<IN?, OUT?> = FlowableTransformer { inFlowable ->
        inFlowable.map { convertInToOut(it) }
    }

    override fun flowOUTtoIN(): FlowableTransformer<OUT?, IN?> = FlowableTransformer { outFlowable ->
        outFlowable.map { convertOutToIn(it) }
    }

    override fun listFlowINtoOUT(): FlowableTransformer<List<IN>?, List<OUT>?> = FlowableTransformer { inFlowable ->
        inFlowable.map { convertListInToOut(it) }
    }

    override fun listFlowOUTtoIN(): FlowableTransformer<List<OUT>?, List<IN>?> = FlowableTransformer { outFlowable ->
        outFlowable.map { convertListOutToIn(it) }
    }

    override fun singleOUTtoIN(): SingleTransformer<OUT?, IN?> = SingleTransformer { it.map { outObject -> convertOutToIn(outObject) } }

    override fun singleINtoOUT(): SingleTransformer<IN?, OUT?> = SingleTransformer { it.map { inObject -> convertInToOut(inObject) } }

    override fun listSingleOUTtoIN(): SingleTransformer<List<OUT>?, List<IN>?> = SingleTransformer {
        it.map { outList -> convertListOutToIn(outList) }
    }

    override fun listSingleINtoOUT(): SingleTransformer<List<IN>?, List<OUT>?> = SingleTransformer {
        it.map { inList -> convertListInToOut(inList) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun jsonArraySingleINtoOUT(): SingleTransformer<JSONArray, List<OUT>> = SingleTransformer {
        it.map { jsonArray ->
            convertListInToOut(
                    mutableListOf<JSONObject>().apply {
                        for (index in (0 until jsonArray.length())) {
                            this.add(jsonArray.optJSONObject(index))
                        }
                    }.toList() as List<IN>)
        }
    }
}
