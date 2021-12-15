import util.Output

/**
 * Run all 25 days at once!
 */
fun main() {
    val startTime = Output.startTime()

    d1_SonarSweep.main()
    d2_Dive.main()
    d3_BinaryDiagnostic.main()
    d4_GiantSquid.main()
    d5_HydrothermalVenture.main()
    d6_Lanternfish.main()
    d7_TheTreacheryOfWhales.main()
    d8_SevenSegmentSearch.main()
    d9_SmokeBasin.main()
    d10_SyntaxScoring.main()
    d11_DumboOctopus.main()
    d12_PassagePathing.main()
    d13_TransparentOrigami.main()
    d14_ExtendedPolymerization.main()

    println()
    Output.executionTime(startTime = startTime, label = "Total execution time")
}