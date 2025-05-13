package com.rmpteam.zozh.data.user

class FakeUserRepository(
    private val fakeUserProfileDatasource: FakeUserProfileDatasource
) : UserRepository {
    override suspend fun findUserByUsername(username: String): Result<UserProfile> {
        return fakeUserProfileDatasource.findUserByUsername(username)
    }

    override suspend fun insertUser(userProfile: UserProfile): Result<UserProfile> {
        return fakeUserProfileDatasource.addUser(userProfile)
    }

    override suspend fun updateUser(userProfile: UserProfile): Result<UserProfile> {
        return fakeUserProfileDatasource.updateUser(userProfile)
    }
}