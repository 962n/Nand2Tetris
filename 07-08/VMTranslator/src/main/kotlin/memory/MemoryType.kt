package memory

sealed class MemoryType {
    abstract class FeatureMemoryType : MemoryType()

    companion object {
        fun of(segment: String): MemoryType? {
            if (DirectFlowMemory.isSame(segment)) {
                return DirectFlowMemory.of(segment)
            }
            if (DirectFixMemory.isSame(segment)) {
                return DirectFixMemory.of(segment)
            }
            if (ConstantMemory.isSame(segment)) {
                return ConstantMemory
            }
            if (StaticMemory.isSame(segment)) {
                return StaticMemory
            }
            return null
        }
    }
}