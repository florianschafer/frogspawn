package net.adeptropolis.nephila.graph.implementations.buffers.sorting;


/*
 * Copyright (C) 2010-2017 Sebastiano Vigna
 * Shamelessly taken and modified to support long indices 2019 by Florian Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** An object that can swap elements whose position is specified by longs. **/

@FunctionalInterface
public interface LongSwapper {
  /** Swaps the data at the given positions.
   *
   * @param a the first position to swap.
   * @param b the second position to swap.
   */
  void swap(long a, long b);
}
