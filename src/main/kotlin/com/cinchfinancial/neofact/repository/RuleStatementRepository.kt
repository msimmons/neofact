package com.cinchfinancial.neofact.repository

import com.cinchfinancial.neofact.model.RuleStatement
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.stereotype.Repository

/**
 * Created by mark on 7/5/16.
 */
@Repository
interface RuleStatementRepository : GraphRepository<RuleStatement> {
}