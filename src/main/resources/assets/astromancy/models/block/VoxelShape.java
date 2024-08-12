Stream.of(
Block.box(12, 1, 3, 13, 5, 13),
Block.box(4, 0, 4, 12, 2, 12),
Block.box(4, 1, 12, 12, 5, 13),
Block.box(3, 1, 3, 4, 5, 13),
Block.box(4, 1, 3, 12, 5, 4)
).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();