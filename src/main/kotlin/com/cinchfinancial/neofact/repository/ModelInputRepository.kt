package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.model.ModelInput
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.stereotype.Repository

/**
 * Created by mark on 7/6/16.
 */
@Repository
interface ModelInputRepository : GraphRepository<ModelInput> {

     fun findByName(name: String) : ModelInput?
}