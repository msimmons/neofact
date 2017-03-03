package com.cinchfinancial.neofact.service

import com.cinchfinancial.neofact.model.ModelInputType

/**
 * Created by mark on 3/1/17.
 */
class YamlInputData {
    lateinit var Name: String
    lateinit var Type: ModelInputType
    var Estimated: Boolean = false
    lateinit var Formula: String
    lateinit var Description: String
}
