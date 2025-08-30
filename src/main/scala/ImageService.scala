package service

import zio.ZIO

trait ImageService {
  def version: ZIO[Any, Nothing, Int]
  def patch: ZIO[Any, Nothing, Int]
}

object ImageService {
  def version = ZIO.serviceWithZIO[ImageService](_.version)
  def patch = ZIO.serviceWithZIO[ImageService](_.patch)

}

class LiveImageService extends ImageService {
  override def version: ZIO[Any, Nothing, Int] = ZIO.succeed(1)
  override def patch: ZIO[Any, Nothing, Int] = ZIO.succeed(2)

}