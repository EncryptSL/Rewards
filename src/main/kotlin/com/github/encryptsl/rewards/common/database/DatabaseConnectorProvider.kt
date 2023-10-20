package com.github.encryptsl.rewards.common.database

interface DatabaseConnectorProvider {

    /**
     * This method connecting to database
     * @param jdbcHost - Host of database, or path to file.
     * @param user - User of database
     * @param pass - Password of database
     */
    fun initConnect(jdbcHost: String, user: String, pass: String)

}