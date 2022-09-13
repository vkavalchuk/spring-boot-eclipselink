/*
 * Copyright 2020 Marc Nuri San Felix
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
 *
 * Created on 2020-06-27, 9:43
 */
package com.marcnuri.demo.springeclipselink;

import com.marcnuri.demo.springeclipselink.repository.Mount;
import com.marcnuri.demo.springeclipselink.repository.MountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mounts")
public class MountResource {

  private final MountDao mountDao;

  @Autowired
  public MountResource(MountDao mountDao) {
    this.mountDao = mountDao;
  }

  @GetMapping
  public Iterable<Mount> getAll() {
    return mountDao.findAll();
  }

    /*
     * Never do that on production
     * Use rows limitation on DB side, i.e 'select * from mount limit 1'
     *
     * It's just for demonstration purposes to show 'eclipselink + spring-data' streaming issue
     */
  @GetMapping("/first")
  @Transactional(readOnly = true)
  public String findFirstMount() {
      final long start = System.currentTimeMillis();
      final String mountName = mountDao
          .streamAllMounts()
          .findFirst()
          .orElseThrow(RuntimeException::new)
          .getName();
      final long duration = System.currentTimeMillis() - start;
      return mountName + "[run query duration = " + duration + "ms]";
  }
}
