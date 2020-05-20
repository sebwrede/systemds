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

package org.apache.sysds.runtime.io;

import org.apache.sysds.conf.ConfigurationManager;
import org.apache.sysds.common.Types.FileFormat;
import org.apache.sysds.conf.CompilerConfig.ConfigType;
import org.apache.sysds.runtime.DMLRuntimeException;
import org.apache.sysds.runtime.data.SparseBlock;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;

public class MatrixReaderFactory 
{
	public static MatrixReader createMatrixReader(FileFormat fmt) {
		MatrixReader reader = null;
		boolean par = ConfigurationManager.getCompilerConfigFlag(ConfigType.PARALLEL_CP_READ_TEXTFORMATS);
		boolean mcsr = MatrixBlock.DEFAULT_SPARSEBLOCK == SparseBlock.Type.MCSR;
		
		switch(fmt) {
			case TEXT:
			case MM:
				reader = (par & mcsr) ?
					new ReaderTextCellParallel(fmt) : new ReaderTextCell(fmt);
				break;
			
			case CSV:
				reader = (par & mcsr) ? 
					new ReaderTextCSVParallel(new FileFormatPropertiesCSV()) :
					new ReaderTextCSV(new FileFormatPropertiesCSV());
				break;
				
			case LIBSVM:
				reader = (par & mcsr) ? 
					new ReaderTextLIBSVMParallel() : new ReaderTextLIBSVM();
				break;
			
			case BINARY:
				reader = (par & mcsr) ? 
					new ReaderBinaryBlockParallel(false) : new ReaderBinaryBlock(false);
				break;
			
			default:
				throw new DMLRuntimeException("Failed to create matrix reader for unknown format: " + fmt.toString());
		}
		return reader;
	}

	public static MatrixReader createMatrixReader( ReadProperties props )  {
		//check valid read properties
		if( props == null )
			throw new DMLRuntimeException("Failed to create matrix reader with empty properties.");
		
		MatrixReader reader = null;
		FileFormat fmt = props.fmt;
		boolean par = ConfigurationManager.getCompilerConfigFlag(ConfigType.PARALLEL_CP_READ_TEXTFORMATS);
		boolean mcsr = MatrixBlock.DEFAULT_SPARSEBLOCK == SparseBlock.Type.MCSR;
		
		switch(fmt) {
			case TEXT:
			case MM:
				reader = (par & mcsr) ?
					new ReaderTextCellParallel(fmt) : new ReaderTextCell(fmt);
				break;
			
			case CSV:
				reader = (par & mcsr) ?
					new ReaderTextCSVParallel( props.formatProperties!=null ?
						(FileFormatPropertiesCSV)props.formatProperties : new FileFormatPropertiesCSV()) :
					new ReaderTextCSV( props.formatProperties!=null ? 
						(FileFormatPropertiesCSV)props.formatProperties : new FileFormatPropertiesCSV());
				break;
		
			case LIBSVM:
				reader = (par & mcsr) ? 
					new ReaderTextLIBSVMParallel() : new ReaderTextLIBSVM();
				break;
				
			case BINARY:
				reader = (par & mcsr) ?
					new ReaderBinaryBlockParallel(props.localFS) : new ReaderBinaryBlock(props.localFS);
				break;
		
			default:
				throw new DMLRuntimeException("Failed to create matrix reader for unknown format: " + fmt.toString());
		}
		return reader;
	}
}