package io.github.pascalklassen.fungalf.persistence.entity

import io.vertx.core.Future

interface Entity<IdentifierT, EntityT> {
    val id: IdentifierT

    fun save(): Future<EntityT>

    fun delete(): Future<EntityT>
}
