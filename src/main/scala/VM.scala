package lambdabot

import com.sun.jersey.core.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import scala.collection.immutable.Stack

import scalaz.NonEmptyList

import VM.Types._
import sun.misc.BASE64Decoder

case class VM(stack: NonEmptyList[(Tick, Opcode)]) {
  def push(o: Opcode)(implicit t: Tick): VM = VM((t, o) <:: stack)

  def peek = stack.head._2

  def serialize(t: Tick): String = {
    // TODO lookup for a set instruction and merge maps
    val newVM = this.push(Set(Map("vm" -> this.b64serialize)))(t)
    newVM.stack.list.filter(_._1 == t).map(_._2.serialize).mkString("|")
  }

  def serialize: String = serialize(tick)

  def tick = stack.head._1

  def b64serialize: String = {
    val baos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(this)
    oos.close()
    new String(Base64.encode(baos.toByteArray))
  }
}

object VM {
  object Types {
    type Tick = Int
  }

  def apply(input: String): Option[VM] = {
    val opcode = Opcode.deserialize(input)

    opcode match {
      case r: React => {
        r.map.get("vm").map(VM.b64deserialize).map { vm =>
          vm.push(r)(vm.tick)
        }.orElse {
          Some(new VM(NonEmptyList((0, r))))
        }
      }
      case _ => None
    }
  }

  private def b64deserialize(s: String): VM = {
    val data = new BASE64Decoder().decodeBuffer(s)
    val ois = new ObjectInputStream(new ByteArrayInputStream(data))
    val vm  = ois.readObject
    ois.close()
    vm.asInstanceOf[VM]
  }
}
