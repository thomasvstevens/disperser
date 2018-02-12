# disperser
_Disperse plate samples via steepest descent._

## Energy functions
* AdjDisperser
  * repel each sample's original neighbors, including original boundaries.
* GroupDisperser
  * repel other samples with the same label
  * equalize boundary probability across groups

## Compile
```bash
javac disperser/*.java
```

## Usage
```bash
java disperser/AdjDisperser <m> <n> <maxSteps>
```
OR
```bash
java disperser/GroupDisperser layout.csv <maxSteps>
```

## Test
```bash
time java disperser/AdjDisperserTest
```
Prints layouts and energies for
1. Default 8x12
2. Single Swap in the 8x12
3. Random 8x12
4. Minimized 4x6 (small)
5. Minimized 8x12 (default)
6. Minimized 16x24 (large)

and shows timing.

* 3min for 16x24 input on Chromebook Intel(R) Celeron(R) CPU N3050 @ 1.60GHz.

```bash
time java disperser/GroupDisperserTest
```
* IN PROGRESS
* Capable of minimizing layout with 4 groups (PQRS.csv) to zero energy.

