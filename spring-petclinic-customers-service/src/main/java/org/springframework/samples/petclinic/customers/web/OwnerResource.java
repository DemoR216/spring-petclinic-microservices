/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.web;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
@Tag(name = "owner-controller", description = "Owner management APIs")
class OwnerResource {

    private static final Logger log = LoggerFactory.getLogger(OwnerResource.class);

    private final OwnerRepository ownerRepository;
    private final OwnerEntityMapper ownerEntityMapper;

    OwnerResource(OwnerRepository ownerRepository, OwnerEntityMapper ownerEntityMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerEntityMapper = ownerEntityMapper;
    }

    @Operation(summary = "Create a new owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Owner created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner createOwner(@Valid @RequestBody OwnerRequest ownerRequest) {
        Owner owner = ownerEntityMapper.map(new Owner(), ownerRequest);
        return ownerRepository.save(owner);
    }

    @Operation(summary = "Find owner by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner found"),
        @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping(value = "/{ownerId}")
    public Optional<Owner> findOwner(@PathVariable("ownerId") @Min(1) int ownerId) {
        return ownerRepository.findById(ownerId);
    }

    @Operation(summary = "List all owners")
    @ApiResponse(responseCode = "200", description = "Owners retrieved successfully")
    @GetMapping
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Operation(summary = "Update an existing owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Owner updated successfully"),
        @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @PutMapping(value = "/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable("ownerId") @Min(1) int ownerId, @Valid @RequestBody OwnerRequest ownerRequest) {
        final Owner ownerModel = ownerRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));

        ownerEntityMapper.map(ownerModel, ownerRequest);
        log.info("Saving owner {}", ownerModel);
        ownerRepository.save(ownerModel);
    }
}
