/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.runtime.privacy.FineGrained;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sysds.runtime.privacy.PrivacyConstraint.PrivacyLevel;

/**
 * Simple implementation of retrieving fine-grained privacy constraints
 * based on pairs in an ArrayList.
 */
public class FineGrainedPrivacyList implements FineGrainedPrivacy {

	private ArrayList<Map.Entry<DataRange, PrivacyLevel>> constraintCollection = new ArrayList<>();

	@Override
	public void put(DataRange dataRange, PrivacyLevel privacyLevel) {
		constraintCollection.add(new AbstractMap.SimpleEntry<DataRange, PrivacyLevel>(dataRange, privacyLevel));
	}

	@Override
	public Map<DataRange,PrivacyLevel> getPrivacyLevel(DataRange searchRange) {
		Map<DataRange, PrivacyLevel> matches = new LinkedHashMap<>();
		for ( Map.Entry<DataRange, PrivacyLevel> constraint : constraintCollection ){
			if ( constraint.getKey().overlaps(searchRange) ) 
				matches.put(constraint.getKey(), constraint.getValue());
		}
		return matches;
	}

	@Override
	public Map<DataRange,PrivacyLevel> getPrivacyLevelOfElement(long[] searchIndex) {
		Map<DataRange, PrivacyLevel> matches = new LinkedHashMap<>();
		constraintCollection.forEach( constraint -> { 
			if (constraint.getKey().contains(searchIndex)) 
				matches.put(constraint.getKey(), constraint.getValue()); 
		} );
		return matches;
	}

	@Override
	public DataRange[] getDataRangesOfPrivacyLevel(PrivacyLevel privacyLevel) {
		ArrayList<DataRange> matches = new ArrayList<>();
		constraintCollection.forEach(constraint -> { if (constraint.getValue() == privacyLevel) matches.add(constraint.getKey()); } );
		return matches.toArray(new DataRange[0]);
	}

	@Override
	public void removeAllConstraints() {
		constraintCollection.clear();
	}

	@Override
	public boolean hasConstraints() {
		return !constraintCollection.isEmpty();
	}
	
}