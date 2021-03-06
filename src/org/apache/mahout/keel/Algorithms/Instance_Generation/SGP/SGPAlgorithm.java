/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

//
//  Main.java
//
//  Isaac Triguero
//
//

package org.apache.mahout.keel.Algorithms.Instance_Generation.SGP;

import org.apache.mahout.keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import org.apache.mahout.keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import org.apache.mahout.keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class SGPAlgorithm extends PrototypeGenerationAlgorithm<SGPGenerator>
{
    /**
     * Builds a new SGPGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SGPGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new SGPGenerator(train, params);    
    }
    
     /**
     * Main method. Executes SGP algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SGPAlgorithm isaak = new SGPAlgorithm();
        isaak.execute(args);
    }
}

